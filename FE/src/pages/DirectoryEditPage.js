import { styled } from "styled-components";

import DirectoryEditBox from "../components/DirectoryEditBox";

const DirectoryEditPage = () => {
  return (
    <>
      <EditWrapper>
        <EditSection>
          <DirectoryEditBox />
        </EditSection>
      </EditWrapper>
    </>
  );
};

export default DirectoryEditPage;

const EditWrapper = styled.div`
  height: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;

  font-family: "HakgyoansimWoojuR";
`;

const EditSection = styled.section`
  width: 100%;
  max-width: 430px;
  height: 100%;

  background-color: #ffffff;

  padding-top: 80px;
  padding-bottom: 130px;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  overflow: auto;
`;
